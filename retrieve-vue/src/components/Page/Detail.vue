<script setup>
import {onMounted, ref} from "vue";
import ParagraphDetail from "@/components/Partment/ParagraphDetail.vue";
import {useRoute} from "vue-router";
import router from "@/router/index.ts";
import SearchService from "@/components/service/searchService.js";

const paragraphs = ref([]);
const recommendList = ref([])
onMounted(async () => {
  let paperId = router.currentRoute.value.query.paperId
  let formData = new FormData()
  formData.append('paperId', paperId)
  console.log(formData)
  await SearchService.detail(formData).then(
      message => {
        paragraphs.value= message
      }
  )
  formData.append('authorId',0)
  formData.append('count', 5)
  formData.append('offset', 1)
  await SearchService.recommend(formData).then(
      message =>{
        console.log(message)
        recommendList.value = message
      }
  )
});
</script>

<template>
  <v-row>
    <v-col cols="2">
      <v-sheet rounded="lg">
        <v-list rounded="lg">
          <v-list-item
              v-for="paragraph in paragraphs"
              :key="paragraph.secNum"
              :title="`${paragraph.secNum}:${paragraph.section}`"
              link
          ></v-list-item>

          <v-divider class="my-2"></v-divider>

        </v-list>
      </v-sheet>
    </v-col>
    <v-col>
      <ParagraphDetail :paragraphs="paragraphs"/>
      <br/>
      <div style="text-align: center;">
        <strong style="font-size: 2em">Recommend List</strong>
      </div>

      <v-list-item
          v-for="paper in recommendList"
          :key="paper.paperId"
          :title=paper.title
          link
      ></v-list-item>
    </v-col>
  </v-row>

</template>

<style scoped>

</style>