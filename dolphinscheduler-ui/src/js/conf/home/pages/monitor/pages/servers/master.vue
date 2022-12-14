/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
<template>
  <m-list-construction :title="'Master ' + $t('Manage')">
    <template slot="content">
      <div class="servers-wrapper" v-show="masterList.length">
        <div class="row-box" v-for="(item,$index) in masterList" :key="$index">
          <div class="row-title">
            <div class="left">
              <span class="sp">Host: {{item.host}}</span>
              <span>{{$t('Zk registration directory')}}: <a href="javascript:" @click="_showZkDirectories(item)" class="links">{{$t('Directory detail')}}</a></span>
            </div>
            <div class="right">
              <span class="sp">{{$t('Create Time')}}: {{item.createTime | formatDate}}</span>
              <span class="sp">{{$t('Last heartbeat time')}}: {{item.lastHeartbeatTime | formatDate}}</span>
            </div>
          </div>
          <div class="row-cont">
            <div class="col-md-3">
              <m-gauge
                      :value="(item.resInfo.cpuUsage * 100).toFixed(2)"
                      :name="'cpuUsage'"
                      :id="'gauge-cpu-' + item.id">
              </m-gauge>
            </div>
            <div class="col-md-3">
              <m-gauge
                      :value="(item.resInfo.memoryUsage * 100).toFixed(2)"
                      :name="'memoryUsage'"
                      :id="'gauge-memory-' + item.id">
              </m-gauge>
            </div>
            <div class="col-md-3">
              <div class="text-num-model">
                <div class="value-p">
                  <strong :style="{color:color[$index]}">{{item.resInfo.diskAvailable.toFixed(2)}}</strong>
                </div>
                <div class="text-1">
                  diskAvailable(GB)
                </div>
              </div>
            </div>
            <div class="col-md-3">
              <div class="text-num-model">
                <div class="value-p">
                  <strong :style="{color:color[$index]}">{{item.resInfo.loadAverage > 0? item.resInfo.loadAverage.toFixed(2):0}}</strong>
                </div>
                <div class="text-1">
                  loadAverage
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <el-drawer
        :visible.sync="drawer"
        :with-header="false">
        <zookeeper-directories-popup :zkDirectories = zkDirectories></zookeeper-directories-popup>
      </el-drawer>
      <div v-if="!masterList.length">
        <m-no-data></m-no-data>
      </div>
      <m-spin :is-spin="isLoading"></m-spin>
    </template>
  </m-list-construction>
</template>
<script>
  import _ from 'lodash'
  import { mapActions } from 'vuex'
  import mGauge from './_source/gauge'
  import mSpin from '@/module/components/spin/spin'
  import mNoData from '@/module/components/noData/noData'
  import themeData from '@/module/echarts/themeData.json'
  import mListConstruction from '@/module/components/listConstruction/listConstruction'
  import zookeeperDirectoriesPopup from './_source/zookeeperDirectories'

  export default {
    name: 'servers-master',
    data () {
      return {
        isLoading: false,
        masterList: [],
        color: themeData.color,
        drawer: false,
        zkDirectories: []
      }
    },
    props: {},
    methods: {
      ...mapActions('monitor', ['getMasterData']),
      _showZkDirectories (item) {
        this.zkDirectories = [{ zkDirectory: item.zkDirectory }]
        this.drawer = true
      }
    },
    watch: {},
    created () {
    },
    mounted () {
      this.isLoading = true
      this.getMasterData().then(res => {
        this.masterList = _.map(res, (v, i) => {
          return _.assign(v, {
            id: v.host + '_' + v.id,
            resInfo: JSON.parse(v.resInfo)
          })
        })
        this.isLoading = false
      }).catch(() => {
        this.isLoading = false
      })
    },
    components: { mListConstruction, mSpin, mNoData, mGauge, zookeeperDirectoriesPopup }
  }
</script>
<style lang="scss" rel="stylesheet/scss">
  @import "./servers";
</style>
