// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: src/main/java/com/dream/flink/pb/proto/SearchResponse.proto

package com.dream.flink.pb;

public final class SearchResponseProto {
  private SearchResponseProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_dream_flink_pb_Result_descriptor;
  static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_dream_flink_pb_Result_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_dream_flink_pb_SearchResponse_descriptor;
  static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_dream_flink_pb_SearchResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n;src/main/java/com/dream/flink/pb/proto" +
      "/SearchResponse.proto\022\022com.dream.flink.p" +
      "b\"6\n\006Result\022\013\n\003url\030\001 \002(\t\022\r\n\005title\030\002 \002(\t\022" +
      "\020\n\010snippets\030\003 \003(\t\"=\n\016SearchResponse\022+\n\007r" +
      "esults\030\001 \003(\0132\032.com.dream.flink.pb.Result" +
      "B+\n\022com.dream.flink.pbB\023SearchResponsePr" +
      "otoP\001"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_com_dream_flink_pb_Result_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_dream_flink_pb_Result_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_com_dream_flink_pb_Result_descriptor,
        new java.lang.String[] { "Url", "Title", "Snippets", });
    internal_static_com_dream_flink_pb_SearchResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_com_dream_flink_pb_SearchResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_com_dream_flink_pb_SearchResponse_descriptor,
        new java.lang.String[] { "Results", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
