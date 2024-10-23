const path = require('path');
const HtmlWebpackPlugin = require("html-webpack-plugin");

module.exports = {
    mode: 'development',
    devtool: 'eval-source-map',
    entry: './src/index.js',
    output: {
        path: path.resolve(__dirname, './dist'),
        filename: 'bundle.js',
    },
    watch: true,
    plugins: [
        new HtmlWebpackPlugin({
            template: path.resolve(__dirname, './src/index.html'),
        })
    ],
    module: {
        rules: [
            {
                test: /\.css$/,  // Look for .css files
                use: ['style-loader', 'css-loader'],  // Apply style-loader and css-loader
            },
            // Other rules...
        ],
    },
};