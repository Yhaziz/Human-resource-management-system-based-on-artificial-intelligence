
/** @type {import('tailwindcss').Config} */
module.exports = {
  presets: [require('@spartan-ng/ui-core/hlm-tailwind-preset')],
  content: [
    './src/**/*.{html,ts}',
    './ui/**/*.{html,ts}',
    "./node_modules/flowbite/**/*.js",
  ],
  theme: {
    extend: {
      cursor: {
        'fancy': 'url(hand.cur), pointer',
      }
    },
  },
  plugins: [
    require('flowbite/plugin')({
      charts: true,
  }),
  ],
};
