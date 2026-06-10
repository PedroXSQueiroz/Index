package br.com.pedroxsqueiroz.Index.repo.converter;

import com.pgvector.PGvector;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * UserType do Hibernate 6 para List<Float> ↔ coluna vector(N) do pgvector.
 *
 * Usa PGvector (com.pgvector) para fazer o bind via setObject, que o driver
 * PostgreSQL reconhece corretamente sem tentar inferir o OID do tipo.
 */
public class EmbeddingVectorConverter implements UserType<List<Float>> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<List<Float>> returnedClass() {
        return (Class<List<Float>>) (Class<?>) List.class;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, List<Float> value, int index,
                            SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            float[] floats = new float[value.size()];
            for (int i = 0; i < value.size(); i++) floats[i] = value.get(i);
            st.setObject(index, new PGvector(floats));
        }
    }

    @Override
    public List<Float> nullSafeGet(ResultSet rs, int position,
                                   SharedSessionContractImplementor session,
                                   Object owner) throws SQLException {
        Object obj = rs.getObject(position);
        if (obj == null) return null;
        String raw = obj.toString().strip();
        // pgvector devolve "[f1,f2,...]"
        String inner = raw.substring(1, raw.length() - 1);
        if (inner.isBlank()) return List.of();
        return Arrays.stream(inner.split(","))
                .map(String::trim)
                .map(Float::parseFloat)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(List<Float> x, List<Float> y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(List<Float> x) {
        return Objects.hashCode(x);
    }

    @Override
    public List<Float> deepCopy(List<Float> value) {
        return value == null ? null : List.copyOf(value);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(List<Float> value) {
        if (value == null) return null;
        return value.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Float> assemble(Serializable cached, Object owner) {
        if (cached == null) return null;
        String raw = cached.toString().strip();
        String inner = raw.substring(1, raw.length() - 1);
        if (inner.isBlank()) return List.of();
        return Arrays.stream(inner.split(","))
                .map(String::trim)
                .map(Float::parseFloat)
                .collect(Collectors.toList());
    }
}
