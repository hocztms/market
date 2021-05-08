package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Address;

import java.util.List;

public interface AddressService {

    RestResult getUserAddress(String username);

    RestResult createAddress(Address address, String username);

    RestResult deleteUserAddressByAddressIds(List<Long> ids, String username);

    RestResult updateUserAddress(Address address, String username);

    List<Address> findAddressByUsername(String username);

    Integer deleteUserAddressById(Long id);

    Integer updateUserAddress(Address address);

    boolean isEmpty(Address address);

    Address findAddressById(Long id);
}
