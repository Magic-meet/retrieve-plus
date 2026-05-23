<template>
  <v-container>
    <v-row>
      <v-col cols="12"  v-for="(paragraph, index) in paragraphs" :key="index">
        <v-card>
          <v-card-title>
            {{ paragraph.secNum }}:   {{ paragraph.section }}
          </v-card-title>
          <v-card-text style="white-space: pre-wrap;">
            {{ paragraph.text }}
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
const props = defineProps({
  paragraphs: {
    type: Array,
    required: true
  }
});
</script>

<style scoped>
/* Add any custom styles here */
</style>
