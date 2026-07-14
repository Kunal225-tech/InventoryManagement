package com.example.inventorymanagement.data

class UserRepository(private val userDao: UserDao) {
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun isEmailExists(email: String) = userDao.isEmailExists(email)
}
