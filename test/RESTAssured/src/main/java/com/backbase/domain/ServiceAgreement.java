package com.backbase.domain;

import java.util.Date;
import java.util.Map;

public class ServiceAgreement {
        public Map<String, String> additions;
        public String id;
        public String externalId;
        public String name;
        public String description;
        public boolean isMaster;
        public Date validFromDate;
        public Date validFromTime;
        public Date validUntilDate;
        public Date validUntilTime;
        public String customerCategory;
}
