/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mdj.base;

public interface BaseLoadingView<T> extends BaseView{
    void showLoading();
    void closeLoading();
    //msg:在显示断网页面的同时，Toast msg消息。
    void showDisconnect(String msg);
    //msg:不显示断网页面，只Toast msg消息。
    void showError(String msg);
    //data:原则上，view显示的所有数据，都应该来自外部
    void updateUI(Object data);
}
