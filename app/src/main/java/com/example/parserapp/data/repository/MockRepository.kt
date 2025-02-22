package com.example.parserapp.data.repository

class MockRepository : MyRepository() {
    override fun getData(): List<String> {
        return listOf("Mock Item 1", "Mock Item 2", "Mock Item 3")
    }
}