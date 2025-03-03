/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.milvus.client;

import com.google.common.util.concurrent.ListenableFuture;
import io.milvus.connection.ClusterFactory;
import io.milvus.connection.ServerSetting;
import io.milvus.grpc.*;
import io.milvus.param.*;
import io.milvus.param.alias.AlterAliasParam;
import io.milvus.param.alias.CreateAliasParam;
import io.milvus.param.alias.DropAliasParam;
import io.milvus.param.collection.*;
import io.milvus.param.control.*;
import io.milvus.param.dml.*;
import io.milvus.param.index.*;
import io.milvus.param.partition.*;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MilvusMultiServiceClient implements MilvusClient {

    private final ClusterFactory clusterFactory;

    /**
     * Sets connect param for multi milvus clusters.
     * @param multiConnectParam multi server connect param
     */
    public MilvusMultiServiceClient(@NonNull MultiConnectParam multiConnectParam) {

        List<ServerSetting> serverSettings = multiConnectParam.getHosts().stream()
                .map(host -> {

                    MilvusClient milvusClient = buildMilvusClient(host, multiConnectParam.getConnectTimeoutMs(),
                            multiConnectParam.getKeepAliveTimeMs(), multiConnectParam.getKeepAliveTimeoutMs(),
                            multiConnectParam.isKeepAliveWithoutCalls(), multiConnectParam.getIdleTimeoutMs());

                    return ServerSetting.newBuilder()
                            .withHost(host)
                            .withMilvusClient(milvusClient).build();

                }).collect(Collectors.toList());

        boolean keepMonitor = serverSettings.size() > 1;

        this.clusterFactory = ClusterFactory.newBuilder()
                .withServerSetting(serverSettings)
                .keepMonitor(keepMonitor)
                .withQueryNodeSingleSearch(multiConnectParam.getQueryNodeSingleSearch())
                .build();
    }

    private MilvusClient buildMilvusClient(ServerAddress host, long connectTimeoutMsm, long keepAliveTimeMs,
                                           long keepAliveTimeoutMs, boolean keepAliveWithoutCalls, long idleTimeoutMs) {
        ConnectParam clusterConnectParam = ConnectParam.newBuilder()
                .withHost(host.getHost())
                .withPort(host.getPort())
                .withConnectTimeout(connectTimeoutMsm, TimeUnit.MILLISECONDS)
                .withKeepAliveTime(keepAliveTimeMs, TimeUnit.MILLISECONDS)
                .withKeepAliveTimeout(keepAliveTimeoutMs, TimeUnit.MILLISECONDS)
                .keepAliveWithoutCalls(keepAliveWithoutCalls)
                .withIdleTimeout(idleTimeoutMs, TimeUnit.MILLISECONDS)
                .build();
        return new MilvusServiceClient(clusterConnectParam);
    }


    @Override
    public MilvusClient withTimeout(long timeout, TimeUnit timeoutUnit) {
        return clusterFactory.getMaster().getClient().withTimeout(timeout, timeoutUnit);
    }

    @Override
    public void close(long maxWaitSeconds) throws InterruptedException {
        this.clusterFactory.getAvailableServerSettings().parallelStream()
                .forEach(serverSetting -> serverSetting.getClient().close());
        this.clusterFactory.close();
    }

    @Override
    public R<Boolean> hasCollection(HasCollectionParam requestParam) {
        return this.clusterFactory.getMaster().getClient().hasCollection(requestParam);
    }

    @Override
    public R<RpcStatus> createCollection(CreateCollectionParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().stream()
                .map(serverSetting -> serverSetting.getClient().createCollection(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<RpcStatus> dropCollection(DropCollectionParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().stream()
                .map(serverSetting -> serverSetting.getClient().dropCollection(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<RpcStatus> loadCollection(LoadCollectionParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().parallelStream()
                .map(serverSetting -> serverSetting.getClient().loadCollection(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<RpcStatus> releaseCollection(ReleaseCollectionParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().stream()
                .map(serverSetting -> serverSetting.getClient().releaseCollection(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<DescribeCollectionResponse> describeCollection(DescribeCollectionParam requestParam) {
        return this.clusterFactory.getMaster().getClient().describeCollection(requestParam);
    }

    @Override
    public R<GetCollectionStatisticsResponse> getCollectionStatistics(GetCollectionStatisticsParam requestParam) {
        return this.clusterFactory.getMaster().getClient().getCollectionStatistics(requestParam);
    }

    @Override
    public R<ShowCollectionsResponse> showCollections(ShowCollectionsParam requestParam) {
        return this.clusterFactory.getMaster().getClient().showCollections(requestParam);
    }

    @Override
    public R<FlushResponse> flush(FlushParam requestParam) {
        List<R<FlushResponse>> response = this.clusterFactory.getAvailableServerSettings().parallelStream()
                .map(serverSetting -> serverSetting.getClient().flush(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<RpcStatus> createPartition(CreatePartitionParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().stream()
                .map(serverSetting -> serverSetting.getClient().createPartition(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<RpcStatus> dropPartition(DropPartitionParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().stream()
                .map(serverSetting -> serverSetting.getClient().dropPartition(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<Boolean> hasPartition(HasPartitionParam requestParam) {
        return this.clusterFactory.getMaster().getClient().hasPartition(requestParam);
    }

    @Override
    public R<RpcStatus> loadPartitions(LoadPartitionsParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().parallelStream()
                .map(serverSetting -> serverSetting.getClient().loadPartitions(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<RpcStatus> releasePartitions(ReleasePartitionsParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().stream()
                .map(serverSetting -> serverSetting.getClient().releasePartitions(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<GetPartitionStatisticsResponse> getPartitionStatistics(GetPartitionStatisticsParam requestParam) {
        return this.clusterFactory.getMaster().getClient().getPartitionStatistics(requestParam);
    }

    @Override
    public R<ShowPartitionsResponse> showPartitions(ShowPartitionsParam requestParam) {
        return this.clusterFactory.getMaster().getClient().showPartitions(requestParam);
    }

    @Override
    public R<RpcStatus> createAlias(CreateAliasParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().stream()
                .map(serverSetting -> serverSetting.getClient().createAlias(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<RpcStatus> dropAlias(DropAliasParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().stream()
                .map(serverSetting -> serverSetting.getClient().dropAlias(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<RpcStatus> alterAlias(AlterAliasParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().stream()
                .map(serverSetting -> serverSetting.getClient().alterAlias(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<RpcStatus> createIndex(CreateIndexParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().parallelStream()
                .map(serverSetting -> serverSetting.getClient().createIndex(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<RpcStatus> dropIndex(DropIndexParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().stream()
                .map(serverSetting -> serverSetting.getClient().dropIndex(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<DescribeIndexResponse> describeIndex(DescribeIndexParam requestParam) {
        return this.clusterFactory.getMaster().getClient().describeIndex(requestParam);
    }

    @Override
    public R<GetIndexStateResponse> getIndexState(GetIndexStateParam requestParam) {
        return this.clusterFactory.getMaster().getClient().getIndexState(requestParam);
    }

    @Override
    public R<GetIndexBuildProgressResponse> getIndexBuildProgress(GetIndexBuildProgressParam requestParam) {
        return this.clusterFactory.getMaster().getClient().getIndexBuildProgress(requestParam);
    }

    @Override
    public R<MutationResult> insert(InsertParam requestParam) {
        List<R<MutationResult>> response = this.clusterFactory.getAvailableServerSettings().parallelStream()
                .map(serverSetting -> serverSetting.getClient().insert(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public ListenableFuture<R<MutationResult>> insertAsync(InsertParam requestParam) {
        List<ListenableFuture<R<MutationResult>>> response = this.clusterFactory.getAvailableServerSettings().parallelStream()
                .map(serverSetting -> serverSetting.getClient().insertAsync(requestParam))
                .collect(Collectors.toList());
        return response.get(0);
    }

    @Override
    public R<MutationResult> delete(DeleteParam requestParam) {
        List<R<MutationResult>> response = this.clusterFactory.getAvailableServerSettings().stream()
                .map(serverSetting -> serverSetting.getClient().delete(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<ImportResponse> importData(@NonNull ImportParam requestParam) {
        List<R<ImportResponse>> response = this.clusterFactory.getAvailableServerSettings().stream()
                .map(serverSetting -> serverSetting.getClient().importData(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<GetImportStateResponse> getImportState(GetImportStateParam requestParam) {
        return this.clusterFactory.getMaster().getClient().getImportState(requestParam);
    }

    @Override
    public R<SearchResults> search(SearchParam requestParam) {
        return this.clusterFactory.getMaster().getClient().search(requestParam);
    }

    @Override
    public ListenableFuture<R<SearchResults>> searchAsync(SearchParam requestParam) {
        return this.clusterFactory.getMaster().getClient().searchAsync(requestParam);
    }

    @Override
    public R<QueryResults> query(QueryParam requestParam) {
        return this.clusterFactory.getMaster().getClient().query(requestParam);
    }

    @Override
    public ListenableFuture<R<QueryResults>> queryAsync(QueryParam requestParam) {
        return this.clusterFactory.getMaster().getClient().queryAsync(requestParam);
    }

    @Override
    public R<CalcDistanceResults> calcDistance(CalcDistanceParam requestParam) {
        return this.clusterFactory.getMaster().getClient().calcDistance(requestParam);
    }

    @Override
    public R<GetMetricsResponse> getMetrics(GetMetricsParam requestParam) {
        return this.clusterFactory.getMaster().getClient().getMetrics(requestParam);
    }

    @Override
    public R<GetFlushStateResponse> getFlushState(GetFlushStateParam requestParam) {
        return this.clusterFactory.getMaster().getClient().getFlushState(requestParam);
    }

    @Override
    public R<GetPersistentSegmentInfoResponse> getPersistentSegmentInfo(GetPersistentSegmentInfoParam requestParam) {
        return this.clusterFactory.getMaster().getClient().getPersistentSegmentInfo(requestParam);
    }

    @Override
    public R<GetQuerySegmentInfoResponse> getQuerySegmentInfo(GetQuerySegmentInfoParam requestParam) {
        return this.clusterFactory.getMaster().getClient().getQuerySegmentInfo(requestParam);
    }

    @Override
    public R<RpcStatus> loadBalance(LoadBalanceParam requestParam) {
        List<R<RpcStatus>> response = this.clusterFactory.getAvailableServerSettings().parallelStream()
                .map(serverSetting -> serverSetting.getClient().loadBalance(requestParam))
                .collect(Collectors.toList());
        return handleResponse(response);
    }

    @Override
    public R<GetCompactionStateResponse> getCompactionState(GetCompactionStateParam requestParam) {
        return this.clusterFactory.getMaster().getClient().getCompactionState(requestParam);
    }

    @Override
    public R<ManualCompactionResponse> manualCompaction(ManualCompactionParam requestParam) {
        return null;
    }

    @Override
    public R<GetCompactionPlansResponse> getCompactionStateWithPlans(GetCompactionPlansParam requestParam) {
        return this.clusterFactory.getMaster().getClient().getCompactionStateWithPlans(requestParam);
    }

    private <T> R<T> handleResponse(List<R<T>> response) {
        if (CollectionUtils.isNotEmpty(response)) {
            R<T> rSuccess = null;
            for (R<T> singleRes : response) {
                if (R.Status.Success.getCode() == singleRes.getStatus()) {
                    rSuccess = singleRes;
                } else {
                    return singleRes;
                }
            }
            if (null != rSuccess) {
                return rSuccess;
            }
        }
        return R.failed(R.Status.Unknown, "Response is empty.");
    }
}

