package com.anjunar.technologyspeaks.olama

import com.fasterxml.jackson.annotation.JsonProperty

import java.time.LocalDateTime
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class AbstractResponse {

  @BeanProperty
  var model: String = uninitialized

  @BeanProperty
  @JsonProperty("created_at")
  var createdAt : LocalDateTime = uninitialized

  @BeanProperty
  @JsonProperty("total_duration")
  var totalDuration: Long = uninitialized

  @BeanProperty
  @JsonProperty("load_duration")
  var loadDuration: Long = uninitialized

  @BeanProperty
  @JsonProperty("prompt_eval_count")
  var promptEvalCount: Long = uninitialized

  @BeanProperty
  @JsonProperty("prompt_eval_duration")
  var promptEvalDuration: Long = uninitialized

  @BeanProperty
  @JsonProperty("eval_count")
  var evalCount: Long = uninitialized

  @BeanProperty
  @JsonProperty("eval_duration")
  var evalDuration: Long = uninitialized


}
