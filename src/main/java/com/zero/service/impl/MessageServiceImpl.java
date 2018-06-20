package com.zero.service.impl;

import com.google.common.collect.Lists;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.MessageRead;
import com.zero.mapper.MessageMapper;
import com.zero.mapper.UserMapper;
import com.zero.model.Message;
import com.zero.model.example.MessageExample;
import com.zero.service.IMessageService;
import com.zero.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class MessageServiceImpl implements IMessageService {

    private static final Integer BATCH_SIZE = 100;
    private static final Logger LOGGER = LogManager.getLogger(MessageServiceImpl.class);
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private IUserService userService;
    @Resource
    private UserMapper userMapper;

    @Override
    public int insert(String title, String content, int type, int sender, List<Integer> receiver) {
        if(CollectionUtils.isEmpty(receiver)){
            return 0;
        }
        try {
            Message message = new Message();
            message.setTitle(title);
            message.setContent(content);
            message.setType(type);
            message.setCreater(sender);
            message.setSender(userService.getUserName(sender));
            if(receiver.size() > BATCH_SIZE){
                int batch = receiver.size() % BATCH_SIZE == 0 ? receiver.size() / BATCH_SIZE : receiver.size() / BATCH_SIZE + 1;
                for (int i = 0; i < batch; i++){
                    List<Integer> tmp = receiver.subList(i * BATCH_SIZE, ((i + 1) * BATCH_SIZE - 1) > (receiver.size() - 1) ? receiver.size() : (i + 1) * BATCH_SIZE );
                    messageMapper.insertBatch(message, tmp);
                }
                return 1;
            }
            return messageMapper.insertBatch(message, receiver);
        } catch (Exception e) {
            LOGGER.info("批量插入站内信消息异常：", e);
            return 0;
        }
    }

    @Override
    public int insert(String title, String content, int type, int sender, String deptCode) {
        if(StringUtils.isEmpty(deptCode)){
            return 0;
        }
        List<Integer> receiver = Lists.newArrayList();
        Stream.of(StringUtils.split(deptCode, ",")).forEach(dept -> {
            receiver.addAll(userMapper.findUserByDeptCode(deptCode));
        });
        return this.insert(title, content, type, sender, receiver);
    }

    @Override
    public long findNotRead(int userId) {
        MessageExample example = new MessageExample();
        MessageExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andIsReadEqualTo(MessageRead.NOT_READ.getKey());
        criteria.andUserIdEqualTo(userId);
        return messageMapper.countByExample(example);
    }

    @Override
    public long findMessageRowNum(int userId, Integer type, Integer read) {
        MessageExample example = new MessageExample();
        MessageExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andUserIdEqualTo(userId);
        if(Objects.nonNull(type)){
            criteria.andTypeEqualTo(type);
        }
        if(Objects.nonNull(read)){
            criteria.andIsReadEqualTo(read);
        }
        return messageMapper.countByExample(example);
    }

    @Override
    public List<Message> findMessagePage(int userId, Integer type, Integer read, Integer pageNum, Integer pageSize) {
        MessageExample example = new MessageExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setLimit(pageSize);
            example.setPage(pageNum);
        }
        MessageExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andUserIdEqualTo(userId);
        if(Objects.nonNull(type)){
            criteria.andTypeEqualTo(type);
        }
        if(Objects.nonNull(read)){
            criteria.andIsReadEqualTo(read);
        }
        return messageMapper.selectByExample(example);
    }

    @Override
    public int markReadMessage(List<Integer> ids) {
        MessageExample example = new MessageExample();
        MessageExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andIdIn(ids);
        Message message = new Message();
        message.setIsRead(MessageRead.READ.getKey());
        message.setUpdateTime(new Date());
        return messageMapper.updateByExampleSelective(message, example);
    }

    @Override
    public Message getMessageById(int id) {
        return messageMapper.selectByPrimaryKey(id);
    }

    @Override
    public int delete(int id) {
        Message message = this.getMessageById(id);
        if (Objects.isNull(message)) {
            return 0;
        }
        message.setIsDeleted(DeletedEnum.YES.getKey());
        message.setUpdateTime(new Date());
        return messageMapper.updateByPrimaryKeySelective(message);
    }

}
