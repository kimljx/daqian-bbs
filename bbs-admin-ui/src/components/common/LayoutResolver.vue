<template>
  <Home v-if="!isStitchRoute" />
  <StitchHome v-else />
</template>

<script>
import Home from './Home.vue'
import StitchHome from './StitchHome.vue'

export default {
  name: 'LayoutResolver',
  components: { Home, StitchHome },
  computed: {
    isStitchRoute() {
      return this.$route.path.startsWith('/stitch-') || this.$route.path.startsWith('/stich-')
    }
  }
}
</script>
