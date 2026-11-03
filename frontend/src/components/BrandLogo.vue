<template>
  <router-link to="/home" class="brand" :class="{ light }">
    <span class="mark">
      <svg viewBox="0 0 48 48" fill="none" aria-hidden="true">
        <ellipse cx="14" cy="17" rx="5.2" ry="6.6" transform="rotate(-20 14 17)" fill="currentColor"/>
        <ellipse cx="24" cy="13" rx="5" ry="6.8" fill="currentColor"/>
        <ellipse cx="34" cy="17" rx="5.2" ry="6.6" transform="rotate(20 34 17)" fill="currentColor"/>
        <ellipse cx="40" cy="27" rx="4" ry="5" transform="rotate(34 40 27)" fill="currentColor"/>
        <path d="M24 22c6.4 0 12 5.1 12 10.6C36 38 31.4 41 24 41s-12-3-12-8.4C12 27.1 17.6 22 24 22Z" fill="currentColor"/>
      </svg>
    </span>
    <span class="words">
      <strong>暖爪之家</strong>
      <small>WARM PAWS RESCUE</small>
    </span>
  </router-link>
</template>

<script>
export default {
  name: 'BrandLogo',
  props: {
    light: { type: Boolean, default: false }
  }
}
</script>

<style scoped>
.brand { display: inline-flex; align-items: center; gap: 11px; color: var(--ink); }
.mark {
  width: 42px; height: 42px; border-radius: 14px;
  background: var(--primary); color: #fff;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 8px 18px rgba(201, 102, 63, 0.22);
}
.mark svg { width: 27px; height: 27px; }
.words { display: flex; flex-direction: column; line-height: 1.1; }
.words strong { font-size: 19px; letter-spacing: 0.06em; }
.words small { font-size: 8px; letter-spacing: 0.16em; color: var(--muted); margin-top: 5px; font-weight: 600; }
.light { color: #fff; }
.light .words small { color: rgba(255,255,255,.55); }
@media (max-width: 420px) {
  .words small { display: none; }
  .words strong { font-size: 17px; }
}
</style>
