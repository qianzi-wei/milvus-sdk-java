/*
  Copyright:©1985-2021 vivo Communication Technology Co., Ltd. All rights reserved.
 
  @Company: 维沃移动通信有限公司
 */
package io.milvus.client;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * MultMilvusClient - TODO
 *
 * @author weiqianzi 11136850
 * @version 1.0
 * @since 5/5/2022 2:56 PM
 */
public class MultMilvusClient implements MilvusClient{



	@Override
	public void close(long maxWaitSeconds) {

	}

	@Override
	public MilvusClient withTimeout(long timeout, TimeUnit timeoutUnit) {
		return null;
	}

	@Override
	public Response createCollection(CollectionMapping collectionMapping) {
		return null;
	}

	@Override
	public HasCollectionResponse hasCollection(String collectionName) {
		return null;
	}

	@Override
	public Response dropCollection(String collectionName) {
		return null;
	}

	@Override
	public Response createIndex(Index index) {
		return null;
	}

	@Override
	public ListenableFuture<Response> createIndexAsync(Index index) {
		return null;
	}

	@Override
	public Response createPartition(String collectionName, String tag) {
		return null;
	}

	@Override
	public HasPartitionResponse hasPartition(String collectionName, String tag) {
		return null;
	}

	@Override
	public ListPartitionsResponse listPartitions(String collectionName) {
		return null;
	}

	@Override
	public Response dropPartition(String collectionName, String tag) {
		return null;
	}

	@Override
	public InsertResponse insert(InsertParam insertParam) {
		return null;
	}

	@Override
	public ListenableFuture<InsertResponse> insertAsync(InsertParam insertParam) {
		return null;
	}

	@Override
	public SearchResponse search(SearchParam searchParam) {
		return null;
	}

	@Override
	public ListenableFuture<SearchResponse> searchAsync(SearchParam searchParam) {
		return null;
	}

	@Override
	public GetCollectionInfoResponse getCollectionInfo(String collectionName) {
		return null;
	}

	@Override
	public ListCollectionsResponse listCollections() {
		return null;
	}

	@Override
	public CountEntitiesResponse countEntities(String collectionName) {
		return null;
	}

	@Override
	public Response getServerStatus() {
		return null;
	}

	@Override
	public Response getServerVersion() {
		return null;
	}

	@Override
	public Response command(String command) {
		return null;
	}

	@Override
	public Response loadCollection(String collectionName) {
		return null;
	}

	@Override
	public Response loadCollection(String collectionName, List<String> partitionTags) {
		return null;
	}

	@Override
	public Response releaseCollection(String collectionName) {
		return null;
	}

	@Override
	public Response releaseCollection(String collectionName, List<String> partitionTags) {
		return null;
	}

	@Override
	public GetIndexInfoResponse getIndexInfo(String collectionName) {
		return null;
	}

	@Override
	public Response dropIndex(String collectionName) {
		return null;
	}

	@Override
	public Response getCollectionStats(String collectionName) {
		return null;
	}

	@Override
	public GetEntityByIDResponse getEntityByID(String collectionName, String partitionTag, List<Long> ids) {
		return null;
	}

	@Override
	public ListIDInSegmentResponse listIDInSegment(String collectionName, String segmentName) {
		return null;
	}

	@Override
	public Response deleteEntityByID(String collectionName, String partitionTag, List<Long> ids) {
		return null;
	}

	@Override
	public Response flush(List<String> collectionNames) {
		return null;
	}

	@Override
	public ListenableFuture<Response> flushAsync(List<String> collectionNames) {
		return null;
	}

	@Override
	public Response flush(String collectionName) {
		return null;
	}

	@Override
	public ListenableFuture<Response> flushAsync(String collectionName) {
		return null;
	}

	@Override
	public Response compact(String collectionName) {
		return null;
	}

	@Override
	public ListenableFuture<Response> compactAsync(String collectionName) {
		return null;
	}
}