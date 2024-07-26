
<!-- resources/views/schools/show.blade.php    ...This a view that displays the graph for a particular school's performance over the years-->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>School Performance</title>
    <link rel="stylesheet" href="{{ asset('css/bootstrap.min.css') }}">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
<div class="container mt-5">
    <h1>Performance of {{ $school->Name }} Over the Last Ten Years</h1>
    <canvas id="performanceChart"></canvas>
    <a href="{{ route('schools.index') }}" class="btn btn-primary mt-3">Back to Schools List</a>
</div>

<script>
document.addEventListener('DOMContentLoaded', function () {
    var ctx = document.getElementById('performanceChart').getContext('2d');

    // Prepare the data
    var data = @json($performanceData);


    // Extract years and average marks
    var years = data.map(item => item.year);
    var averageMarks = data.map(item => item.averagemarks);

    // Generate a full range of years for the last 10 years
    var startYear = new Date().getFullYear() - 10;
    var endYear = new Date().getFullYear();
    var allYears = [];
    var allAverageMarks = [];

    for (var year = startYear; year <= endYear; year++) {
        allYears.push(year);
        var index = years.indexOf(year);
        allAverageMarks.push(index >= 0 ? averageMarks[index] : 0); // Default to 0 if no data for the year
    }

    // Create chart
    var chart = new Chart(ctx, 
    
    {
        type: 'bar',
        data: {
            labels: allYears,
            datasets: [{
                label: 'Average Marks',
                data: allAverageMarks,
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1
            }]
        },


        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Average Marks'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Year'
                    }
                }
            }
        }
    });
});
</script>
</body>
</html>
