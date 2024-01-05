package com.fattanaufal.storyapp.paging

import android.location.Geocoder
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.fattanaufal.storyapp.local.RemoteKeys
import com.fattanaufal.storyapp.local.StoryDatabase
import com.fattanaufal.storyapp.local.StoryEntity
import com.fattanaufal.storyapp.preference.UserPreferences
import com.fattanaufal.storyapp.story.ApiStoryService
import com.fattanaufal.storyapp.utils.wrapEspressoIdlingResource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator (
    private val storyDatabase: StoryDatabase,
    private val apiStoryService: ApiStoryService,
    private val userPreferences: UserPreferences,
    private val geocoder: Geocoder,
    private val locationDefault: String
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>,
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }


        try {
            val responseData =
                apiStoryService.getAllStoriesWithLocation(userPreferences.getSessionUser().first().token, page, state.config.pageSize).listStory
            val endOfPaginationReached = responseData.isEmpty()

            val stories = responseData.map {
                StoryEntity(
                    it.id,
                    it.name,
                    it.description,
                    it.photoUrl,
                    it.createdAt,
                    it.lat,
                    it.lon,
                    getCityAndProvince(LatLng(it.lat, it.lon))
                )
            }

            storyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyDatabase.storyDao().deleteAll()
                    storyDatabase.remoteKeysDao().deleteRemoteKeys()
                }


                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }


                storyDatabase.remoteKeysDao().insertAll(keys)
                storyDatabase.storyDao().insertStories(stories)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: Exception) {

            return MediatorResult.Error(exception)

        }

    }

    private fun getCityAndProvince(latLng: LatLng): String {
        wrapEspressoIdlingResource {
            var cityAndProvince = locationDefault
            val latitude = latLng.latitude
            val longitude = latLng.longitude

            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        cityAndProvince = "${addresses[0].subAdminArea}, ${addresses[0].adminArea}"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return cityAndProvince
        }
    }


    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}