package life.tc.community.service;

import life.tc.community.dto.NotificationDTO;
import life.tc.community.dto.PaginationDTO;
import life.tc.community.dto.QuestionDTO;
import life.tc.community.enums.NotificationStatusEnum;
import life.tc.community.enums.NotificationTypeEnum;
import life.tc.community.exception.CustomErrorCode;
import life.tc.community.exception.CustomizeException;
import life.tc.community.mapper.NotificationMapper;
import life.tc.community.mapper.UserMapper;
import life.tc.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();

        //问题所有的数量

        //Integer totalCount = questionMapper.countByUserId(userId);
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId);
        Integer totalCount = (int)notificationMapper.countByExample(notificationExample);

        //计算总共需要多少页
        Integer totalPage;
        if(totalCount % size == 0) {
            totalPage = totalCount / size;
        }
        else{
            totalPage = totalCount / size + 1;
        }

        if(page <1){
            page = 1;
        }
        if(page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage,page);


        //size*(page-1),offset为每页第一个问题的编号
        Integer offset = size * (page-1);
        //List<Question> questions = questionMapper.listByUserId(userId,offset,size);
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverEqualTo(userId);
        //拿到所有通知
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size));
        //if(notifications.size() == 0){
           // return paginationDTO;
        //}

        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        //将拿到的List notifications转换成 notificationDTOS
        for(Notification notification:notifications){
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));

            notificationDTOS.add(notificationDTO);
        }

        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    //获得未读通知的数量
    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        //只拿到未读消息的数量
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    //跳转到点击消息产生的问题界面
    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if(notification == null){
            throw new CustomizeException(CustomErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if(notification.getReceiver() != user.getId()){
            throw new CustomizeException(CustomErrorCode.READ_NOTIFICATION_FAIL);
        }
        //若无问题，点击去之后就改成已读
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
