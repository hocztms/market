package com.hocztms.service;

import com.hocztms.entity.Illegal;

public interface IllegalUserService {

    Illegal findIllegalUserByUsername(String username);

    Integer deleteIllegalUserByUsername(String username);

    Integer updateIllegalUserStatusByUsername(String username, int status);

    Integer updateIllegalUserNumByUsername(String username);
}
