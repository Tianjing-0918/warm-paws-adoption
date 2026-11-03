<template>
  <router-link :to="'/pet/' + pet.id" class="pet-card">
    <div class="cover">
      <img :src="cover" :alt="pet.name" loading="lazy" />
      <span class="state" :class="'state-' + pet.status">
        <i class="status-dot"></i>{{ statusLabel }}
      </span>
      <span class="fav" @click.prevent.stop>
        <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21.2l7.8-7.7 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg>
      </span>
    </div>
    <div class="body">
      <div class="row">
        <h3>{{ pet.name }}</h3>
        <span class="age">{{ ageText(pet.age) }}</span>
      </div>
      <p class="meta">{{ pet.categoryName || '等待确认品种' }} · {{ pet.gender || '未知' }}<template v-if="pet.color"> · {{ pet.color }}</template></p>
      <div class="tags">
        <span v-for="tag in tags" :key="tag" class="mini-tag">{{ tag }}</span>
      </div>
      <div class="foot">
        <span class="loc">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.4"/></svg>
          {{ shortLocation }}
        </span>
        <span class="go">
          了解它
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
        </span>
      </div>
    </div>
  </router-link>
</template>

<script>
import { imageUrl } from '../utils/api'
import { ageText, statusText } from '../utils/demo'

export default {
  name: 'PetCard',
  props: { pet: { type: Object, required: true } },
  computed: {
    cover() {
      if (this.pet.images) return imageUrl(this.pet.images)
      return this.pet.gender === '公' ? '/media/animals/pet-02-juzi.jpg' : '/media/animals/pet-01-mimi.jpg'
    },
    statusLabel() { return statusText(this.pet.status) },
    shortLocation() {
      const loc = this.pet.location || '暖爪救助站'
      return loc.replace('上海市 · ', '').replace('上海市', '上海')
    },
    tags() {
      if (this.pet.traits && this.pet.traits.length) return this.pet.traits.slice(0, 3)
      const tags = []
      if (this.pet.vaccinated) tags.push('已疫苗')
      if (this.pet.sterilized) tags.push('已绝育')
      tags.push(this.pet.weight ? this.pet.weight + ' kg' : '待了解')
      return tags.slice(0, 3)
    }
  },
  methods: { ageText }
}
</script>

<style scoped>
.pet-card {
  display: block;
  background: var(--paper);
  border: 1px solid var(--line);
  border-radius: 24px;
  overflow: hidden;
  transition: transform 0.35s cubic-bezier(.2,.7,.2,1), box-shadow 0.35s, border-color 0.35s;
}
.pet-card:hover { transform: translateY(-7px); box-shadow: var(--shadow); border-color: var(--line-strong); }
.cover { position: relative; aspect-ratio: 4 / 4.6; overflow: hidden; background: var(--cream-2); }
.cover img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.7s cubic-bezier(.2,.7,.2,1); }
.pet-card:hover .cover img { transform: scale(1.06); }
.state {
  position: absolute; top: 14px; left: 14px;
  display: inline-flex; align-items: center; gap: 6px;
  padding: 6px 12px; border-radius: 999px;
  background: rgba(255,255,255,.92); backdrop-filter: blur(8px);
  font-size: 11px; font-weight: 700; color: var(--sage-dark);
  box-shadow: 0 4px 12px rgba(0,0,0,.08);
}
.state-1 { color: #8a6a2a; background: rgba(251,240,220,.95); }
.state-2 { color: #3f6b8a; background: rgba(232,241,248,.95); }
.state-3 { color: #777; background: rgba(245,245,245,.95); }
.fav {
  position: absolute; top: 12px; right: 12px; width: 34px; height: 34px;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  background: rgba(255,255,255,.86); color: var(--ink-2); opacity: 0; transform: translateY(-4px);
  transition: all .25s;
}
.pet-card:hover .fav { opacity: 1; transform: none; }
.body { padding: 17px 18px 18px; }
.row { display: flex; align-items: baseline; justify-content: space-between; gap: 10px; }
h3 { font-size: 20px; letter-spacing: -.02em; }
.age { font-size: 12px; color: var(--muted); white-space: nowrap; }
.meta { font-size: 12.5px; color: var(--muted); margin-top: 3px; }
.tags { display: flex; gap: 6px; margin-top: 12px; flex-wrap: wrap; }
.mini-tag { font-size: 10.5px; color: var(--sage-dark); background: var(--sage-soft); padding: 3px 8px; border-radius: 999px; }
.foot { display: flex; align-items: center; justify-content: space-between; margin-top: 15px; padding-top: 13px; border-top: 1px solid var(--line); }
.loc { display: inline-flex; align-items: center; gap: 4px; font-size: 11.5px; color: var(--muted); }
.go { display: inline-flex; align-items: center; gap: 5px; font-size: 12px; font-weight: 600; color: var(--primary); }
</style>
