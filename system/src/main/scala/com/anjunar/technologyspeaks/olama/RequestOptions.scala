package com.anjunar.technologyspeaks.olama

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

case class RequestOptions(num_ctx : Int = 2048,
                          repeat_last_n : Int = 64,
                          repeat_penalty : Float = 1.1,
                          temperature : Float = 0.0,
                          seed : Int = 42,
                          stop : String = null,
                          num_predict : Int = 256,
                          top_k : Int = 64,
                          top_p : Float = 0.95,
                          min_p : Float = 0.0)