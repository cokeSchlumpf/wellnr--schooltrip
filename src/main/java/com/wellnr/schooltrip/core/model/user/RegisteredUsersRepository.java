package com.wellnr.schooltrip.core.model.user;

public interface RegisteredUsersRepository extends RegisteredUsersReadRepository {

    void insertOrUpdate(RegisteredUser registeredUser);

}
