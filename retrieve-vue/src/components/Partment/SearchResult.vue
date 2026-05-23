<template>
  <v-container>
    <v-row>
      <v-col cols="12" v-for="(paper, index) in papers" :key="index">
        <v-card @click="detail(paper.paperId)">
          <v-card-title>
            {{ paper.title }}
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="auto">
                <v-icon>mdi-account</v-icon> {{ paper.authors }}
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="auto">
                <v-icon>mdi-calendar</v-icon> {{ paper.year }}
              </v-col>
              <v-col cols="auto">
                <v-icon>mdi-newspaper</v-icon> {{ paper.venue }}
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="auto">
                <strong>Keywords：</strong> {{ paper.keywords }}
              </v-col>
            </v-row>
            <v-row>
              <v-col>
                <strong>Abstract：</strong> {{ paper.abstractContent }}
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import {useRouter} from "vue-router";
import SearchService from "@/components/service/searchService.js";
import router from "@/router/index.ts";
import Deatil from "@/components/Page/Detail.vue";
import Detail from "@/components/Page/Detail.vue";

const props = defineProps({
  papers: {
    type: Array,
    required: true
  }
});

const detail = async (paperId)=>{
  await router.push({
    path:'/detail',
    query:{paperId:paperId}
  });
}
</script>

<style scoped>
/* Add any custom styles here */
</style>
