package messenger.util;

import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import messenger.model.ChatRoom;

public class MybatisQueryChatRoom {
	String resource;
	private SqlSessionFactory sqlSessionFactory = null;
	private Logger log = Logger.getLogger(this.getClass());

	public MybatisQueryChatRoom(String resource) {
		super();
		this.resource = resource;

		try {
			InputStream inputStream = Resources.getResourceAsStream(resource);
			this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// Create Room
	public void insertChatRoom(int roomNum, String roomMasterId, String roomTitle, String serverIP,
			int serverLocalPort) {
		ChatRoom chatroom = new ChatRoom(roomNum, roomMasterId, roomTitle, 1, serverIP, serverLocalPort);
		int count = -1;
		SqlSession session = sqlSessionFactory.openSession();

		try {
			count = session.insert("chatroomMapper.insertChatRoom", chatroom);
		} finally {
			session.commit();
			session.close();
		}
		log.debug(count + "건 삽입했습니다.");
	}

	// Select All
	public List<ChatRoom> selectAll() {
		List<ChatRoom> list = null;
		SqlSession session = sqlSessionFactory.openSession();

		try {
			list = session.selectList("chatroomMapper.selectAll");
		} finally {
			session.close();
		}
		return list;
	}

	public ChatRoom selectOne(int roomNum) {
		ChatRoom chatRoom = new ChatRoom();
		chatRoom.setRoomNum(roomNum);

		SqlSession session = sqlSessionFactory.openSession();

		try {
			chatRoom = session.selectOne("chatroomMapper.selectChatRoom", chatRoom);
		} finally {
			session.close();
		}

		return chatRoom;
	}

	// Delete Room
	public void deleteChatRoom(String roomMasterId) {
		int count = -1;
		ChatRoom chatroom = new ChatRoom();
		chatroom.setRoomMasterId(roomMasterId);
		SqlSession session = sqlSessionFactory.openSession();

		try {
			count = session.delete("chatroomMapper.deleteChatRoom", chatroom);
			log.debug(count + "건 삭제했습니다.");
		} finally {
			session.commit();
			session.close();
		}

	}

	// 대화방 수 체크
	public int countRoomNumber() {

		int cnt = 0;
		List<ChatRoom> list = null;
		log.debug("여기까지 와라");
		SqlSession session = sqlSessionFactory.openSession();

		try {
			list = session.selectList("chatroomMapper.selectAll");
		} finally {
			session.close();
		}
		cnt = list.size();
		log.debug("count = " + cnt);
		return cnt;

	}

	// 대화방 내 인원 체크
	public int countRoomParticipant(String roomMasterId) {
		int cnt = 0;
		ChatRoom chatRoom = new ChatRoom();
		chatRoom.setRoomMasterId(roomMasterId);

		SqlSession session = sqlSessionFactory.openSession();

		try {
			chatRoom = session.selectOne("chatroomMapper.selectMember", chatRoom);
		} finally {
			session.close();
		}
		return cnt;
	}

	// 대화방 존재 유무 확인
	public boolean checkExistingRoom(int roomNum) {
		List<ChatRoom> list = null;
		SqlSession session = sqlSessionFactory.openSession();

		try {
			list = session.selectList("chatroomMapper.selectAll");
		} finally {
			session.close();
		}
		for (int cnt = 0; cnt < list.size(); cnt++) {
			if (roomNum == list.get(cnt).getRoomNum())
				return true;
		}
		return false;

	}

}
