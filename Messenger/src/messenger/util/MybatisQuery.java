package messenger.util;

import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import messenger.model.Member;

public class MybatisQuery {
	String resource;
	private SqlSessionFactory sqlSessionFactory = null;
	private Logger log = Logger.getLogger(this.getClass());

	public MybatisQuery(String resource) {
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
	public void insertMember(String id, String password) {
		Member member = new Member(id, password);
		int count = -1;
		SqlSession session = sqlSessionFactory.openSession();

		try {
			count = session.insert("memberMapper.insertMember", member);
		} finally {
			session.commit();
			session.close();
		}
		log.debug(count + "건 삽입했습니다.");
	}

	// Select All
	public List<Member> selectAll() {
		List<Member> list = null;
		SqlSession session = sqlSessionFactory.openSession();

		try {
			list = session.selectList("memberMapper.selectAll");
		} finally {
			session.close();
		}
		// System.out.println(list);
		return list;
	}

	public Member selectOne(String id) {
		Member member = new Member();
		member.setId(id);

		SqlSession session = sqlSessionFactory.openSession();

		try {
			member = session.selectOne("memberMapper.selectMember", member);
		} finally {
			session.close();
		}

		return member;
	}

	public void update(String id, String password) {
		Member member = new Member(id, password);
		int count = -1;
		SqlSession session = sqlSessionFactory.openSession();

		try {
			count = session.update("memberMapper.updatePassword", member);
			log.debug(count + "건 수정했습니다.");
		} finally {
			session.commit();
			session.close();
		}

	}

	public void delete(String id) {
		int count = -1;
		Member member = new Member();
		member.setId(id);
		SqlSession session = sqlSessionFactory.openSession();

		try {
			count = session.delete("memberMapper.deleteMember", member);
			log.debug(count + "건 삭제했습니다.");
		} finally {
			session.commit();
			session.close();
		}

	}

	// 회원가입 시 중복 아이디 검사 & 회원탈퇴 시 아이디 존재 여부 검사
	public boolean checkExistingID(String id) {
		Member member = new Member();
		member.setId(id);

		SqlSession session = sqlSessionFactory.openSession();

		try {
			List<Member> list = session.selectList("memberMapper.selectAll");
			for (Member memberlist : list) {
				if (id.equals(memberlist.getId()))
					return true; // 중복된 아이디 존재시 true 반환 -> if문에 걸리게 만듬
			}
		} finally {
			session.close();
		}

		return false;
	}

	// 회원탈퇴 시 패스워드 일치 검사
	public boolean checkPassword(String id, String password) {
		Member member = new Member();
		member.setId(id);

		SqlSession session = sqlSessionFactory.openSession();

		try {
			member = session.selectOne("memberMapper.selectMember", member);
			if (password.equals(member.getPassword()))
				return true; // 패스워드 일치 시 true값 반환

		} finally {
			session.close();
		}

		return false; // 패스워드 불일치 시 false값 반환

	}

}
