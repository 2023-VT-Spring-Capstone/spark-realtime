package com.capstone.realtime.util

import com.capstone.realtime.bean.{PostLog, PostInfo}

import java.lang.reflect.{Field, Method, Modifier}
import scala.util.control.Breaks

/**
 * Object copy
 */
object MyBeanUtils {

  def main(args: Array[String]): Unit = {
    val postLog: PostLog =
      PostLog("Roy", null, null, null, null, null, null, null, null, 0L, 0F, 12345, null, null, null, null, 0F, 0F, 0F, null)
    val pageInfo: PostInfo = new PostInfo()
    println("before copy: " + pageInfo)
    copyProperties(postLog, pageInfo)
    println("after copy: " + pageInfo)
  }

  /**
   * copy all attributes from srcObj to destObj's attributes
   */
  def copyProperties(srcObj : AnyRef , destObj: AnyRef): Unit ={
    if(srcObj == null || destObj == null ){
        return
    }
    val srcFields: Array[Field] = srcObj.getClass.getDeclaredFields
    //handle property copy
    for (srcField <- srcFields) {
      Breaks.breakable{
        //getMethodName
        var getMethodName : String = srcField.getName
        //setMethodName
        var setMethodName : String = srcField.getName+"_$eq"
        val getMethod: Method = srcObj.getClass.getDeclaredMethod(getMethodName)
        val setMethod: Method =
        try {
          destObj.getClass.getDeclaredMethod(setMethodName, srcField.getType)
        } catch {
          //NoSuchMethodException
          case ex : Exception =>  Breaks.break()
        }
        //ignore val property
        val destField: Field = destObj.getClass.getDeclaredField(srcField.getName)
        if(destField.getModifiers.equals(Modifier.FINAL)){
          Breaks.break()
        }
        setMethod.invoke(destObj, getMethod.invoke(srcObj))
      }
    }
  }
}

