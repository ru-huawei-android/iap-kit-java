/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.codelabs.iap.example.huawei;

public class Key {
    private static final String publicKey = "MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAmWI6tq9OsUI2Tyea45T9CKgQtVOPZJ1CM2N/YDa928JKHj0HinMkFgB+iZszA5xvIdH1O+jGDoyW2ecf5KRssr4ekWeDbf3S3b/05pz5C1a6sOeUv5/8kTZsPVKQPqQlFME3nMfOCRWrom3MnLKpLFXs1YB+QfiGhaPqPpXljkYFMrSvucfasEMa+2fnrQqMmTqBAyGEhBlimN6O2V8eGXa8+VGK9zjNzPwnViSmhz+QQLymyAEo6GznGSxppfkBkVvjsm7kilyd9YVvDwMXyqdMEMBjNQQS4Mgdlh1qnfnpqQnEbWk17jqtMxkSrOq/lDO38T8jnJywBUcmnJZHgzCxIpOM2620p6ks+4GcA/PuFsdN5dJ54M/4AGkyNIBBS0cD9z7Tix6TIH/gv27136Pv0H5BZpLRKRy93q4BRxpsDplknE3kV0klcaQy4nZnyEv9DKessDh7bHEOYexJdTUD4c/O89A7EjmW5FFo68y+OdxKIRAfQK5DGzpz+9pzAgMBAAE=";
    /**
     * get the publicKey of the application
     * During the encoding process, avoid storing the public key in clear text.
     * @return
     */
    public static String getPublicKey(){
        return publicKey;
    }
}
