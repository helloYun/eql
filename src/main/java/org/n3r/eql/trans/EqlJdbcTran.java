package org.n3r.eql.trans;

import org.n3r.eql.Eql;
import org.n3r.eql.EqlTran;
import org.n3r.eql.ex.EqlExecuteException;
import org.n3r.eql.util.EqlUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class EqlJdbcTran implements EqlTran {
    private final Eql eql;
    private Connection connection;

    public EqlJdbcTran(Eql eql, Connection connection) {
        this.eql = eql;
        this.connection = connection;
    }

    @Override
    public void start() {
        try {
            if (connection == null) throw new EqlExecuteException(
                    "EqlJdbcTran could not start transaction. " +
                            " Cause: The DataSource returned a null connection.");

            if (connection.getAutoCommit()) connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new EqlExecuteException(e);
        }
    }

    @Override
    public void commit() {
        if (connection == null) return;

        try {
            connection.commit();
        } catch (SQLException e) {
            throw new EqlExecuteException(e);
        }
    }

    @Override
    public void rollback() {
        if (connection == null) return;

        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new EqlExecuteException(e);
        }
    }

    @Override
    public Connection getConn() {
        return connection;
    }

    /**
     * Oracle JDBC will auto commit when close without explicit commit/rollback.
     */
    @Override
    public void close() throws IOException {
        EqlUtils.closeQuietly(connection);
        eql.resetTran();
    }

}
