const path = require('path');
const HtmlWebpackPlugin = require("html-webpack-plugin");

module.exports = {
    mode: 'development',
    devtool: 'eval-source-map',
    entry: {
        index: './src/index.js',  // Entry for index.js
        interface: './src/interface.js',  // Entry for interface.js
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: '[name].js',
        clean: true,
    },
    watch: true,
    plugins: [
        new HtmlWebpackPlugin({
            template: path.resolve(__dirname, './src/index.html'),
            chunks: ['index'],
        }),
        new HtmlWebpackPlugin({
            filename: 'interface.html',
            template: path.resolve(__dirname, './src/interface.html'),
            chunks: ['interface'],
        }),
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