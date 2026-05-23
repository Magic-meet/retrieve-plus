<script setup>

import {ref} from "vue";


// 页面选择
const choices = [
  {title:'检索',router:'/home',color:'blue'},
  {title: '文档',router: '/papers',color:'green'},
  {title: '接口文档',router: '/user',color: 'red'}
]


// 主题色
const color = ref('blue')

// 按钮点击事件
const bottomClick = (pageColor)=>{
  color.value = pageColor
}
</script>

<template>
  <v-app id="inspire">

    <v-app-bar flat :color="color">
      <v-container class="mx-auto d-flex align-center justify-center">
        <v-btn
          v-for="choice in choices"
          color="white"
          :key="choice.color"
          :text="choice.title"
          :to="choice.router"
          variant="text"
          @click="bottomClick(choice.color)"
        ></v-btn>
        <v-spacer></v-spacer>


      </v-container>
    </v-app-bar>

    <v-main class="bg-grey-lighten-3">
      <v-container>
        <v-row>
          <v-col>
            <v-sheet
                min-height="70vh"
                rounded="lg"
            >
              <RouterView />
            </v-sheet>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<style scoped>

</style>
