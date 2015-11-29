package com.atoz.dao;

import com.atoz.model.User;
import com.atoz.model.UserInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Sergiu on 09.11.2015.
 */
@Repository
public class UserDAOImpl implements UserDAO {

  private Log log = LogFactory.getLog(UserDAOImpl.class);

  String selectRoles = "select r.role as role from users u " +
      "inner join user_roles ur on ur.user_id = u.id " +
      "inner join roles r on ur.role_id = r.id " +
      "where u.login=:login";

  String selectUser = "select u.login as login, u.password as password from users u where u.login=:login";

  String selectUserInformation = "select ud.user_id, ud.first_name, ud.last_name, ud.serial_number, ud.email from user_details ud " +
      " inner join users u on ud.user_id=u.id " +
      " where u.login=:login";

  NamedParameterJdbcTemplate template;

  public void setDataSource(DataSource dataSource) {
    template = new NamedParameterJdbcTemplate(dataSource);
  }

  @Override
  public User getUser(String login) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("login", login);
    User user = null;
    try {
      user = template.queryForObject(selectUser, params, new UserRowMapper());
      if (user != null) {
        List<String> roles = template.query(selectRoles, params, new RowMapper<String>() {
          @Override
          public String mapRow(ResultSet resultSet, int i) throws SQLException {
            return resultSet.getString(1);
          }
        });
        for (String role : roles) {
          user.addRole(role);
        }
      }
    } catch (DataAccessException ex) {
      log.error("Failed to load user data: " + ex);
    }
    return user;
  }

  @Override
  public UserInformation getUserInformation(String login) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("login", login);
    UserInformation userInformation = null;
    try {
      userInformation = template.queryForObject(selectUserInformation, params, new UserDetailsRowMapper());
    } catch (DataAccessException ex) {
      log.error("Failed to load user information: " + ex);
    }
    return userInformation;
  }

  private static final class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
      return new User(resultSet.getString(1), resultSet.getString(2));
    }
  }

  private static final class UserDetailsRowMapper implements RowMapper<UserInformation> {
    @Override
    public UserInformation mapRow(ResultSet resultSet, int i) throws SQLException {
      return new UserInformation(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
    }
  }

}
