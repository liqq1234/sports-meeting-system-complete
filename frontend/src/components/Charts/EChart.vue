<template>
  <div ref="chartRef" :style="{ height, width }"></div>
</template>

<script>
import * as echarts from 'echarts'
import { debounce } from '@/utils/debounce'

export default {
  name: 'EChart',
  props: {
    width: { type: String, default: '100%' },
    height: { type: String, default: '350px' },
    options: { type: Object, default: () => ({}) }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    options: {
      handler(val) {
        this.setOptions(val)
      },
      deep: true
    }
  },
  mounted() {
    this.initChart()
    this._resizeHandler = debounce(() => {
      if (this.chart) {
        this.chart.resize()
      }
    }, 200)
    window.addEventListener('resize', this._resizeHandler)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this._resizeHandler)
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.chartRef)
      this.setOptions(this.options)
    },
    setOptions(options) {
      if (this.chart && options) {
        this.chart.setOption(options, true)
      }
    }
  }
}
</script>
