public class ReMappedStatement {
    private String sqlId;
    private String sql;
    private String resultType;
    private String parameterType;
    private String sqlType;

    public ReMappedStatement(String sqlId, String sql, String resultType, String parameterType, String sqlType) {
        this.sqlId = sqlId;
        this.sql = sql;
        this.resultType = resultType;
        this.parameterType = parameterType;
        this.sqlType = sqlType;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    @Override
    public String toString() {
        return "ReMappedStatement{" +
                "sqlId='" + sqlId + '\'' +
                ", sql='" + sql + '\'' +
                ", resultType='" + resultType + '\'' +
                ", parameterType='" + parameterType + '\'' +
                ", sqlType='" + sqlType + '\'' +
                '}';
    }
}
