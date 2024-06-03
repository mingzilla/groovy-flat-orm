package uk.co.mingzilla.flatorm.domain.conn

import groovy.json.JsonSlurper
import uk.co.mingzilla.flatorm.util.DomainUtil

class ConnectionDetail {

    String driverClassName
    String url
    String user
    String password

    private ConnectionDetail() {}

    static ConnectionDetail createFromPath(String path) {
        File file = new File(path)
        Map json = new JsonSlurper().parse(file) as Map
        return create(json)
    }

    static ConnectionDetail create(Map data) {
        ConnectionDetail detail = new ConnectionDetail()
        DomainUtil.mergeFields(detail, data)
        return detail
    }

    Properties getConnProperties() {
        Properties properties = new Properties()
        properties.setProperty('user', user)
        properties.setProperty('password', password)
        return properties
    }
}
