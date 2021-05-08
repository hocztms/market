package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.entity.Illegal;
import com.hocztms.mapper.IllegalMapper;
import com.hocztms.service.IllegalUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IllegalUserServiceImpl implements IllegalUserService {


    @Autowired
    private IllegalMapper illegalMapper;
    @Override
    public Illegal findIllegalUserByUsername(String username) {
        QueryWrapper<Illegal> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        return illegalMapper.selectOne(wrapper);
    }

    @Override
    public Integer deleteIllegalUserByUsername(String username) {
        QueryWrapper<Illegal> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        return illegalMapper.delete(wrapper);
    }

    @Override
    public Integer updateIllegalUserStatusByUsername(String username,int status) {
        QueryWrapper<Illegal> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        Illegal illegal = findIllegalUserByUsername(username);
        illegal.setStatus(status);
        return illegalMapper.update(illegal,wrapper);
    }

    @Override
    public Integer updateIllegalUserNumByUsername(String username) {
        QueryWrapper<Illegal> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        Illegal illegal = findIllegalUserByUsername(username);
        if (illegal==null) {
            illegalMapper.insert(new Illegal(username,1,1,1));
            return 1;
        }
        int num = illegal.getNum();
        illegal.setNum(num+1);
        return illegalMapper.update(illegal,wrapper);
    }
}
