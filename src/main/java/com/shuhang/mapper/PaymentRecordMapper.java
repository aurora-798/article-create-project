package com.shuhang.mapper;

import com.mybatisflex.core.BaseMapper;
import com.shuhang.model.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付记录 Mapper
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
}
