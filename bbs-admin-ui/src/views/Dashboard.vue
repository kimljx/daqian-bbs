<template>
  <div class="welcome-page">
    <h1 class="welcome-text">欢迎使用</h1>
  </div>
</template>

<script>
export default {
  name: 'dashboard'
};
</script>

<style scoped>
.welcome-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

.welcome-text {
  font-size: 48px;
  font-weight: 500;
  color: #333;
  margin: 0;
}
</style>
