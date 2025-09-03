package com.hotel.room;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Room Service Test Suite")
@SelectPackages({
    "com.hotel.room.service",
    "com.hotel.room.controller",
    "com.hotel.room.repository"
})
@IncludeClassNamePatterns(".*Test")
public class RoomServiceTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
