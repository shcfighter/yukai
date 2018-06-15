package com.zero.service;

import com.zero.model.Message;

import java.util.List;

public interface IMessageService {

    int insert(String title, String content, int type, int sender, List<Integer> receiver);

    int insert(String title, String content, int type, int sender, String deptCode);

    long findNotRead(int userId);

    long findMessageRowNum(int userId, Integer type, Integer read);

    List<Message> findMessagePage(int userId, Integer type, Integer read, Integer pageNum, Integer pageSize);

    int markReadMessage(List<Integer> ids);

    Message getMessageById(int id);

    int delete(int id);
}
