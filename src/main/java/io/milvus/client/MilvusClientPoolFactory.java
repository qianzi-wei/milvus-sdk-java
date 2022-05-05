package io.milvus.client;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * MilvusClientPool - TODO
 *
 */
public class MilvusClientPoolFactory implements PooledObjectFactory<MilvusClient> {

    @Override
    public void activateObject(PooledObject<MilvusClient> p) throws Exception {
    }

    @Override
    public void destroyObject(PooledObject<MilvusClient> p) throws Exception {
        p.getObject().close();
    }

 @Override
	public PooledObject<MilvusClient> makeObject() throws Exception {
		ConnectParam param = new ConnectParam.Builder().withPort().withHost().build();
		MilvusClient client = new MilvusGrpcClient(param);
		return new DefaultPooledObject<>(client);
	}

    @Override
    public void passivateObject(PooledObject<MilvusClient> p) throws Exception {

    }

    @Override
    public boolean validateObject(PooledObject<MilvusClient> p) {
        try {
            Response serverStatus = p.getObject().getServerStatus();
            if (serverStatus == null || !serverStatus.ok()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}