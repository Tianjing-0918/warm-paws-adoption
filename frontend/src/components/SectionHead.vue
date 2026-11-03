<template>
  <div class="section-head">
    <div>
      <span v-if="eyebrow" class="eyebrow">{{ eyebrow }}</span>
      <h2 class="section-title">{{ title }}</h2>
      <p v-if="sub" class="section-sub">{{ sub }}</p>
    </div>
    <router-link v-if="actionText && actionTo" :to="actionTo" class="link-more">
      {{ actionText }}
      <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
    </router-link>
    <slot name="action" />
  </div>
</template>

<script>
export default {
  name: 'SectionHead',
  props: {
    eyebrow: { type: String, default: '' },
    title: { type: String, required: true },
    sub: { type: String, default: '' },
    actionText: { type: String, default: '' },
    actionTo: { type: [String, Object], default: '' }
  }
}
</script>
