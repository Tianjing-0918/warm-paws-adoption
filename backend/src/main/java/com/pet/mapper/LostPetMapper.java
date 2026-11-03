package com.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.entity.LostPet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LostPetMapper extends BaseMapper<LostPet> {
}
