# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

add_library(
        PrefixSearch
        STATIC
        DecodingException.cpp
        Distanse.cpp
        EngineFacade.cpp
        PrefixNode.cpp
        PrefixTree.cpp
        SearchEng.cpp
        SentenseExtractor.cpp
        Utils.cpp
        WordIndexer.cpp
)