package com.backbase.domain;

import java.util.List;

public class User {
        public String id;
        public Long createdTimestamp;
        public String username;
        public boolean enabled;
        public boolean totp;
        public boolean emailVerified;
        public String firstName;
        public String lastName;
        public String email;
        public UserAttributes attributes;
        public List<Object> disableableCredentialTypes;
        public List<Object> requiredActions;
        public int notBefore;
        UserManageGroupMembership access;
}
