dataPoints = 280;

[senderData] = ReadDataFromFile(".\measurements\SDS.txt", dataPoints, 2);
[receiverData] = ReadDataFromFile(".\measurements\RDS1.txt", dataPoints, 2);

xAxis = 1:280;


senderHeader = senderData(:,1);
senderTime = senderData(:,2);


receiverHeader = receiverData(:,1);
receiverTime = receiverData(:,2);

timediff = zeros(1, dataPoints);

for i=1:dataPoints
    if receiverHeader(i)~=senderHeader(i)
        disp(i)
    end
    timediff(i) = receiverTime(i) - senderTime(i);
end

subplot(2, 1, 1) %row, column, position
plot(xAxis, timediff) % Plot all four lines in one call to plot
grid on
title('Graph to show the difference in delay between using different sizes of interleaving')
xlabel('Block')
ylabel('Delay')

