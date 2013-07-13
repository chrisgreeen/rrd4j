package org.rrd4j.core;

import java.io.IOException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/**
 * {@link org.rrd4j.core.RrdBackendFactory} that uses <a href="http://www.mongodb.org/">MongoDB</a> for data storage. Construct a
 * MongoDB {@link com.mongodb.DBCollection} and pass it via the constructor.
 *
 * @author Mathias Bogaert
 */
public class RrdMongoDBBackendFactory extends RrdBackendFactory {
    private final DBCollection rrdCollection;

    /**
     * Creates a RrdMongoDBBackendFactory. Make sure that the passed {@link com.mongodb.DBCollection} has a safe write
     * concern, is capped (if needed) and slaveOk() called if applicable.
     *
     * @param rrdCollection the collection to use for storing RRD byte data
     */
    public RrdMongoDBBackendFactory(DBCollection rrdCollection) {
        this.rrdCollection = rrdCollection;

        // make sure we have an index on the path field
        rrdCollection.ensureIndex(new BasicDBObject("path", 1));

        // set the RRD backend factory
        RrdBackendFactory.registerAndSetAsDefaultFactory(this);
    }

    /** {@inheritDoc} */
    @Override
    protected RrdBackend open(String path, boolean readOnly) throws IOException {
        return new RrdMongoDBBackend(path, rrdCollection);
    }

    /** {@inheritDoc} */
    @Override
    protected boolean exists(String path) throws IOException {
        BasicDBObject query = new BasicDBObject();
        query.put("path", path);
        return rrdCollection.findOne(query) != null;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean shouldValidateHeader(String path) throws IOException {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "MONGODB";
    }
}
