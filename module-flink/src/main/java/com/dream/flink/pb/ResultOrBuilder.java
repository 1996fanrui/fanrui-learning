// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: src/main/java/com/dream/flink/pb/proto/SearchResponse.proto

package com.dream.flink.pb;

public interface ResultOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.dream.flink.pb.Result)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>required string url = 1;</code>
   */
  boolean hasUrl();
  /**
   * <code>required string url = 1;</code>
   */
  java.lang.String getUrl();
  /**
   * <code>required string url = 1;</code>
   */
  com.google.protobuf.ByteString
      getUrlBytes();

  /**
   * <code>required string title = 2;</code>
   */
  boolean hasTitle();
  /**
   * <code>required string title = 2;</code>
   */
  java.lang.String getTitle();
  /**
   * <code>required string title = 2;</code>
   */
  com.google.protobuf.ByteString
      getTitleBytes();

  /**
   * <code>repeated string snippets = 3;</code>
   */
  com.google.protobuf.ProtocolStringList
      getSnippetsList();
  /**
   * <code>repeated string snippets = 3;</code>
   */
  int getSnippetsCount();
  /**
   * <code>repeated string snippets = 3;</code>
   */
  java.lang.String getSnippets(int index);
  /**
   * <code>repeated string snippets = 3;</code>
   */
  com.google.protobuf.ByteString
      getSnippetsBytes(int index);
}
