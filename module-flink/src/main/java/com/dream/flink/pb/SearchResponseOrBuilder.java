// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: src/main/java/com/dream/flink/pb/proto/SearchResponse.proto

package com.dream.flink.pb;

public interface SearchResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.dream.flink.pb.SearchResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .com.dream.flink.pb.Result results = 1;</code>
   */
  java.util.List<com.dream.flink.pb.Result> 
      getResultsList();
  /**
   * <code>repeated .com.dream.flink.pb.Result results = 1;</code>
   */
  com.dream.flink.pb.Result getResults(int index);
  /**
   * <code>repeated .com.dream.flink.pb.Result results = 1;</code>
   */
  int getResultsCount();
  /**
   * <code>repeated .com.dream.flink.pb.Result results = 1;</code>
   */
  java.util.List<? extends com.dream.flink.pb.ResultOrBuilder> 
      getResultsOrBuilderList();
  /**
   * <code>repeated .com.dream.flink.pb.Result results = 1;</code>
   */
  com.dream.flink.pb.ResultOrBuilder getResultsOrBuilder(
      int index);
}