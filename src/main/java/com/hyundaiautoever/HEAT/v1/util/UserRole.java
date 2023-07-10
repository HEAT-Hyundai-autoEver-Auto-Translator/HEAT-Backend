package com.hyundaiautoever.HEAT.v1.util;

public enum UserRole {
    user{
        @Override
        public String toString() {
            return "user";
        }
    },
    admin{
        @Override
        public String toString() {
            return "admin";
        }
    }
}
