package com.hackyeah.sl.backend.constant;

import org.springframework.stereotype.Service;

@Service
public class Authority {
  public static final String[] USER_AUTHORITIES = {"user:read"};
  public static final String[] ADMIN_AUTHORITIES = {"user:read", "user:update", "user:create"};
  public static final String[] SUPER_ADMIN_AUTHORITIES = {
    "user:read", "user:update", "user:create", "user:delete"
  };
}
