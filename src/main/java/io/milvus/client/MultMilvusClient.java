package io.milvus.client;

import com.google.common.util.concurrent.ListenableFuture;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * MultMilvusClient
 */
public class MultMilvusClient implements MilvusClient {

    private final GenericObjectPool<MilvusClient> pool;

    /**
     * master rw client, some write use it
     */
    private final MilvusClient master;

    public MultMilvusClient(MilvusClient master, MilvusClientPoolFactory factory,
            GenericObjectPoolConfig<MilvusClient> config) {
        this.pool = new GenericObjectPool<MilvusClient>(factory, config);
        this.master = master;
    }

    @Override
    public void close(long maxWaitSeconds) {
        // do nothing
    }

    @Override
    public MilvusClient withTimeout(long timeout, TimeUnit timeoutUnit) {
        // do nothing
        return null;
    }

    @Override
    public Response createCollection(CollectionMapping collectionMapping) {
        return master.createCollection(collectionMapping);
    }

    @Override
    public HasCollectionResponse hasCollection(String collectionName) {
        try {
            MilvusClient client = pool.borrowObject();
            HasCollectionResponse response = client.hasCollection(collectionName);
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response dropCollection(String collectionName) {
        return master.dropCollection(collectionName);
    }

    @Override
    public Response createIndex(Index index) {
        return master.createIndex(index);
    }

    @Override
    public ListenableFuture<Response> createIndexAsync(Index index) {
        return master.createIndexAsync(index);
    }

    @Override
    public Response createPartition(String collectionName, String tag) {
        return master.createPartition(collectionName, tag);
    }

    @Override
    public HasPartitionResponse hasPartition(String collectionName, String tag) {
        try {
            MilvusClient client = pool.borrowObject();
            HasPartitionResponse response = client.hasPartition(collectionName, tag);
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ListPartitionsResponse listPartitions(String collectionName) {
        try {
            MilvusClient client = pool.borrowObject();
            ListPartitionsResponse response = client.listPartitions(collectionName);
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response dropPartition(String collectionName, String tag) {
        return master.dropPartition(collectionName, tag);
    }

    @Override
    public InsertResponse insert(InsertParam insertParam) {
        return master.insert(insertParam);
    }

    @Override
    public ListenableFuture<InsertResponse> insertAsync(InsertParam insertParam) {
        return master.insertAsync(insertParam);
    }

    @Override
    public SearchResponse search(SearchParam searchParam) {
        try {
            MilvusClient client = pool.borrowObject();
            SearchResponse response = client.search(searchParam);
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ListenableFuture<SearchResponse> searchAsync(SearchParam searchParam) {
        try {
            MilvusClient client = pool.borrowObject();
            ListenableFuture<SearchResponse> response = client.searchAsync(searchParam);
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GetCollectionInfoResponse getCollectionInfo(String collectionName) {
        try {
            MilvusClient client = pool.borrowObject();
            GetCollectionInfoResponse response = client.getCollectionInfo(collectionName);
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ListCollectionsResponse listCollections() {
        try {
            MilvusClient client = pool.borrowObject();
            ListCollectionsResponse response = client.listCollections();
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CountEntitiesResponse countEntities(String collectionName) {
        try {
            MilvusClient client = pool.borrowObject();
            CountEntitiesResponse response = client.countEntities(collectionName);
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response getServerStatus() {
        try {
            MilvusClient client = pool.borrowObject();
            Response response = client.getServerStatus();
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response getServerVersion() {
        try {
            MilvusClient client = pool.borrowObject();
            Response response = client.getServerVersion();
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response command(String command) {
        return master.command(command);
    }

    @Override
    public Response loadCollection(String collectionName) {
        return master.loadCollection(collectionName);
    }

    @Override
    public Response loadCollection(String collectionName, List<String> partitionTags) {
        return master.loadCollection(collectionName, partitionTags);
    }

    @Override
    public Response releaseCollection(String collectionName) {
        return master.releaseCollection(collectionName);
    }

    @Override
    public Response releaseCollection(String collectionName, List<String> partitionTags) {
        return master.releaseCollection(collectionName, partitionTags);
    }

    @Override
    public GetIndexInfoResponse getIndexInfo(String collectionName) {
        try {
            MilvusClient client = pool.borrowObject();
            GetIndexInfoResponse response = client.getIndexInfo(collectionName);
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response dropIndex(String collectionName) {
        return master.dropIndex(collectionName);
    }

    @Override
    public Response getCollectionStats(String collectionName) {
        try {
            MilvusClient client = pool.borrowObject();
            Response response = client.getCollectionStats(collectionName);
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GetEntityByIDResponse getEntityByID(String collectionName, String partitionTag, List<Long> ids) {
        try {
            MilvusClient client = pool.borrowObject();
            GetEntityByIDResponse response = client.getEntityByID(collectionName, partitionTag, ids);
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ListIDInSegmentResponse listIDInSegment(String collectionName, String segmentName) {
        try {
            MilvusClient client = pool.borrowObject();
            ListIDInSegmentResponse response = client.listIDInSegment(collectionName, segmentName);
            pool.returnObject(client);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response deleteEntityByID(String collectionName, String partitionTag, List<Long> ids) {
        return master.deleteEntityByID(collectionName, partitionTag, ids);
    }

    @Override
    public Response flush(List<String> collectionNames) {
        return master.flush(collectionNames);
    }

    @Override
    public ListenableFuture<Response> flushAsync(List<String> collectionNames) {
        return master.flushAsync(collectionNames);
    }

    @Override
    public Response flush(String collectionName) {
        return master.flush(collectionName);
    }

    @Override
    public ListenableFuture<Response> flushAsync(String collectionName) {
        return master.flushAsync(collectionName);
    }

    @Override
    public Response compact(String collectionName) {
        return master.compact(collectionName);
    }

    @Override
    public ListenableFuture<Response> compactAsync(String collectionName) {
        return master.compactAsync(collectionName);
    }
}