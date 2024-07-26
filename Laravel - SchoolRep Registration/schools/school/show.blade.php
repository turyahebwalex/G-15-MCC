<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Participant Performance</title>
    <link rel="stylesheet" href="{{ asset('css/bootstrap.min.css') }}">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
<div class="container mt-5">
    <h1>Performance of {{ $participant->FirstName }} {{ $participant->LastName }}</h1>
    <canvas id="performanceChart"></canvas>
    <!--<a href="{{ route('school.showParticipants', $participant->schoolRegNo) }}" class="btn btn-primary mt-3">Back to Participants List</a>-->
    <a href="{{ url()->previous() }}" class="btn btn-primary mt-3">Back to Participants List</a>
</div>

<script>
document.addEventListener('DOMContentLoaded', function () {
    var ctx = document.getElementById('performanceChart').getContext('2d');

    // Prepare the data
    var data = @json($performanceData);

    // Extract years and percentage scores
    var years = data.map(item => item.year);
    var percentageScores = data.map(item => item.percentageScore);

    // Generate different colors for each bar
    var backgroundColors = years.map(() => {
        return `rgba(${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, 0.2)`;
    });

    var borderColors = backgroundColors.map(color => {
        return color.replace('0.2', '1');
    });

    // Create chart
    var chart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: years,
            datasets: [{
                label: 'Percentage Score',
                data: percentageScores,
                backgroundColor: backgroundColors,
                borderColor: borderColors,
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Percentage Score'
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