#include "native_search.h"

#include <jni.h>
#include <stdio.h>
#include <string>
#include <iostream>
#include <exception>
#include <stdexcept>
#include "prefixsearch/EngineFacade.h"
#include "prefixsearch/DecodingException.h"

std::string jstring2string(JNIEnv* env, jstring jStr);

JNIEXPORT jlong JNICALL Java_com_yingenus_pocketchinese_functions_search_PrefixSearcher_newNative
        (JNIEnv * jnienv, jobject jobject){
    return (long) new EngineFacade;
}

/*
 * Class:     com_yingenus_pocketchinese_functions_search_PrefixSearcher
 * Method:    init
 * Signature: (Ljava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_com_yingenus_pocketchinese_functions_search_PrefixSearcher_init
(JNIEnv * jnienv, jobject jobject, jstring jstring, jlong pointer){
    try
    {
        EngineFacade* eng = (EngineFacade*) pointer;
        std::string file_name = jstring2string(jnienv, jstring);
        eng->init(file_name);
    }
    catch (const DecodingException& dexp) {
        jclass expt = jnienv->FindClass("com/yingenus/pocketchinese/functions/search/IndexDecodingException");
        jnienv->ThrowNew(expt, dexp.what());
    }
    catch (const std::ios_base::failure& failure){
        jclass exception = jnienv->FindClass("java/io/IOException");
        jnienv->ThrowNew(exception, failure.what());
    }
    catch (const std::exception& excpt) {
        jclass exception = jnienv->FindClass("java/lang/Exception");
        jnienv->ThrowNew(exception, excpt.what());
    }
}

/*
 * Class:     com_yingenus_pocketchinese_functions_search_PrefixSearcher
 * Method:    setLanguage
 * Signature: (IJ)V
 */
JNIEXPORT void JNICALL Java_com_yingenus_pocketchinese_functions_search_PrefixSearcher_setLanguage
(JNIEnv * jnienv, jobject jobject, jint jint, jlong pointer){

    EngineFacade* eng = (EngineFacade*)pointer;

    EngineFacade::language lang;

    if (jint == com_yingenus_pocketchinese_functions_search_PrefixSearcher_RUSSIAN)
    {
        lang = EngineFacade::language::RUSSIAN;
    }
    else if (jint == com_yingenus_pocketchinese_functions_search_PrefixSearcher_PINYIN) {
        lang = EngineFacade::language::PINYIN;
    }
    else{
        lang = EngineFacade::language::DEFAULT;
    }

    eng->setLang(lang);
}

/*
 * Class:     com_yingenus_pocketchinese_functions_search_PrefixSearcher
 * Method:    find
 * Signature: ([BJ)[I
 */
JNIEXPORT jintArray JNICALL Java_com_yingenus_pocketchinese_functions_search_PrefixSearcher_find
        (JNIEnv * jnienv, jobject jobject, jbyteArray jbyteArray, jlong pointer){
    try
    {
        size_t length = (size_t)jnienv->GetArrayLength(jbyteArray);
        jbyte* pBytes = jnienv->GetByteArrayElements(jbyteArray, NULL);

        std::string str = std::string((char*)pBytes, length);;
        jnienv->ReleaseByteArrayElements(jbyteArray, pBytes, JNI_ABORT);

        EngineFacade* eng = (EngineFacade*)pointer;

        std::vector<int>* ids = eng->find(str);

        jintArray idsArray = jnienv->NewIntArray(ids->size());
        jint* arr = jnienv->GetIntArrayElements(idsArray, NULL);

        for (int i = 0; i < ids->size(); i++) {
            arr[i] = (*ids)[i];
        }

        jnienv->ReleaseIntArrayElements(idsArray, arr, NULL);

        delete ids;

        return idsArray;
    }
    catch (const DecodingException& dexp) {
        jclass expt = jnienv->FindClass("com/yingenus/pocketchinese/functions/search/IndexDecodingException");
        jnienv->ThrowNew(expt, dexp.what());
    }
    catch (const std::ios_base::failure& failure) {
        jclass excpt = jnienv->FindClass("java/io/IOException");
        jnienv->ThrowNew(excpt, failure.what());
    }
    catch (const std::logic_error *log_error) {
        jclass exception = jnienv->FindClass("java/lang/IllegalStateException");
        jnienv->ThrowNew(exception, log_error->what());
    }
    catch (const std::exception& excpt)
    {
        jclass excp = jnienv->FindClass("java/lang/Exception");
        jnienv->ThrowNew(excp, excpt.what());
    }
    return jnienv->NewIntArray(0);
}

/*
 * Class:     com_yingenus_pocketchinese_functions_search_PrefixSearcher
 * Method:    deleteNative
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_yingenus_pocketchinese_functions_search_PrefixSearcher_deleteNative
(JNIEnv * jnienv, jobject jobject, jlong pointer){
    EngineFacade* eng = (EngineFacade*)pointer;
    delete eng;
}

std::string jstring2string(JNIEnv* env, jstring jStr) {
    if (!jStr)
        return "";

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray)env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("US-ASCII"));

    size_t length = (size_t)env->GetArrayLength(stringJbytes);
    jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string((char*)pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}
