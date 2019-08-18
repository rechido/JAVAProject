package messenger.util;

import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import messenger.model.LoginMember;

public class MybatisQueryLoginMember {
	String resource;
	private SqlSessionFactory sqlSessionFactory = null;
	private Logger log = Logger.getLogger(this.getClass());

	public MybatisQueryLoginMember(String resource) {
		super();
		this.resource = resource;

		try {
			InputStream inputStream = Resources.getResourceAsStream(resource);
			this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// Insert Query
	public void insertMember(String id, int roomNum, String status) {
		LoginMember loginMember = new LoginMember(id, roomNum, status);
		int count = -1;
		SqlSession session = sqlSessionFactory.openSession();

		try {
			count = session.insert("loginMemberMapper.insertMember", loginMember);
		} finally {
			session.commit();
			session.close();
		}
		log.debug(count + "건 삽입했습니다.");
	}

	// Select All
	public List<LoginMember> selectAll() {
		List<LoginMember> list = null;
		SqlSession session = sqlSessionFactory.openSession();

		try {
			list = session.selectList("loginMemberMapper.selectAll");
		} finally {
			session.close();
		}
		// System.out.println(list);
		return list;
	}

	// Select All in chatroom
	public List<LoginMember> selectAllinChatRoom(int roomNum) {
		List<LoginMember> list = null;
		SqlSession session = sqlSessionFactory.openSession();
		LoginMember member = new LoginMember();
		member.setRoomNum(roomNum);
		try {
			list = session.selectList("loginMemberMapper.selectAllinChatRoom", member);
		} finally {
			session.close();
		}
		// System.out.println(list);
		return list;
	}

	// Select One
	public LoginMember selectOne(String id) {
		LoginMember member = new LoginMember();
		member.setId(id);

		SqlSession session = sqlSessionFactory.openSession();

		try {
			member = session.selectOne("loginMemberMapper.selectMember", member);
		} finally {
			session.close();
		}

		return member;
	}

	public int selectRoomNum(String id) {
		int roomNum;
		LoginMember member = new LoginMember();
		member.setId(id);

		SqlSession session = sqlSessionFactory.openSession();

		try {
			member = session.selectOne("loginMemberMapper.selectMember", member);
		} finally {
			session.close();
		}

		roomNum = member.getRoomNum();

		return roomNum;
	}

	public String selectStatus(String id) {
		String status;
		LoginMember member = new LoginMember();
		member.setId(id);

		SqlSession session = sqlSessionFactory.openSession();

		try {
			member = session.selectOne("loginMemberMapper.selectMember", member);
		} finally {
			session.close();
		}

		status = member.getStatus();

		return status;
	}

	public void delete(String id) {
		int count = -1;
		LoginMember member = new LoginMember();
		member.setId(id);
		SqlSession session = sqlSessionFactory.openSession();

		try {
			count = session.delete("loginMemberMapper.deleteMember", member);
			log.debug(count + "건 삭제했습니다.");
		} finally {
			session.commit();
			session.close();
		}

	}

	public void updateRoomNum(String id, int roomNum) {
		LoginMember member = new LoginMember();
		member.setId(id);
		member.setRoomNum(roomNum);
		int count = -1;
		SqlSession session = sqlSessionFactory.openSession();

		try {
			count = session.update("loginMemberMapper.updateRoomNum", member);
			log.debug(count + "건 수정했습니다.");
		} finally {
			session.commit();
			session.close();
		}
	}

	public void updateStatus(String id, String status) {
		LoginMember member = new LoginMember();
		member.setId(id);
		member.setStatus(status);
		int count = -1;
		SqlSession session = sqlSessionFactory.openSession();

		try {
			count = session.update("loginMemberMapper.updateStatus", member);
			log.debug(count + "건 수정했습니다.");
		} finally {
			session.commit();
			session.close();
		}
	}

	// 중복 로그인 검사
	public boolean checkExistingID(String id) {
		LoginMember loginMember = new LoginMember();
		loginMember.setId(id);

		SqlSession session = sqlSessionFactory.openSession();

		try {
			List<LoginMember> list = session.selectList("loginMemberMapper.selectAll");
			for (LoginMember memberlist : list) {
				if (id.equals(memberlist.getId()))
					return true; // 중복된 아이디 존재시 true 반환 -> if문에 걸리게 만듬
			}
		} finally {
			session.close();
		}

		return false;
	}

}
